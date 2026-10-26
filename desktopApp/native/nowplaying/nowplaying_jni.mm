// macOS Now Playing, for the desktop player.
//
// MPNowPlayingInfoCenter is what puts a track in Control Center and on the
// lock screen, and MPRemoteCommandCenter is what the media keys and the
// AirPods talk to. Both are Objective-C, which the JVM cannot reach, so this
// is the arrangement the Windows SMTC library already uses: a small library
// beside the application, spoken to over JNI.
//
// The same library answers the app's AppleScript commands (`playpause`,
// `next track` and so on, declared in BitChord.sdef). Media key routers such
// as Threek use them to reach this app in particular rather than whichever
// app macOS currently considers "now playing".
//
// Everything that touches AppKit or MediaPlayer is done on the main queue.

#include <jni.h>

#import <AppKit/AppKit.h>
#import <Foundation/Foundation.h>
#import <MediaPlayer/MediaPlayer.h>

namespace {

JavaVM* g_vm = nullptr;
jclass g_type = nullptr;
jmethodID g_on_button = nullptr;
jmethodID g_on_seek = nullptr;

// Numbers shared with DesktopMacMedia.kt.
constexpr jint kButtonPlay = 0;
constexpr jint kButtonPause = 1;
constexpr jint kButtonNext = 2;
constexpr jint kButtonPrevious = 3;
constexpr jint kButtonStop = 4;
constexpr jint kButtonPlayPause = 5;

// The four-character codes in BitChord.sdef.
constexpr AEEventClass kSuite = 'BtCd';
constexpr AEEventID kEventPlayPause = 'PlPs';
constexpr AEEventID kEventPlay = 'Play';
constexpr AEEventID kEventPause = 'Paus';
constexpr AEEventID kEventNext = 'Next';
constexpr AEEventID kEventPrevious = 'Prev';

// Bumped on every track change, so artwork that arrives late for the previous
// track is dropped rather than shown over the current one.
NSUInteger g_generation = 0;
NSString* g_art_url = nil;
MPMediaItemArtwork* g_artwork = nil;

NSMutableArray* g_targets = nil;

JNIEnv* attach() {
    if (g_vm == nullptr) return nullptr;
    JNIEnv* env = nullptr;
    if (g_vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_8) == JNI_OK) return env;
    if (g_vm->AttachCurrentThreadAsDaemon(reinterpret_cast<void**>(&env), nullptr) != JNI_OK) return nullptr;
    return env;
}

void send_button(jint button) {
    JNIEnv* env = attach();
    if (env == nullptr || g_type == nullptr || g_on_button == nullptr) return;
    env->CallStaticVoidMethod(g_type, g_on_button, button);
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
}

void send_seek(jlong position_ms) {
    JNIEnv* env = attach();
    if (env == nullptr || g_type == nullptr || g_on_seek == nullptr) return;
    env->CallStaticVoidMethod(g_type, g_on_seek, position_ms);
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
}

// Read as UTF-16 rather than through GetStringUTFChars: that is JNI's modified
// UTF-8, which NSString refuses for anything outside the BMP, so a title with
// an emoji in it came out empty.
NSString* to_ns(JNIEnv* env, jstring value) {
    if (value == nullptr) return @"";
    jsize length = env->GetStringLength(value);
    const jchar* chars = env->GetStringChars(value, nullptr);
    if (chars == nullptr) return @"";
    NSString* result = [NSString stringWithCharacters:reinterpret_cast<const unichar*>(chars)
                                               length:static_cast<NSUInteger>(length)];
    env->ReleaseStringChars(value, chars);
    return result ?: @"";
}

void add_target(MPRemoteCommand* command, jint button) {
    command.enabled = YES;
    id target = [command addTargetWithHandler:^MPRemoteCommandHandlerStatus(MPRemoteCommandEvent*) {
        send_button(button);
        return MPRemoteCommandHandlerStatusSuccess;
    }];
    [g_targets addObject:@[ command, target ]];
}

}  // namespace

@interface BitChordScriptHandler : NSObject
- (void)handle:(NSAppleEventDescriptor*)event withReply:(NSAppleEventDescriptor*)reply;
@end

@implementation BitChordScriptHandler
- (void)handle:(NSAppleEventDescriptor*)event withReply:(NSAppleEventDescriptor*)reply {
    switch (event.eventID) {
        case kEventPlayPause: send_button(kButtonPlayPause); break;
        case kEventPlay: send_button(kButtonPlay); break;
        case kEventPause: send_button(kButtonPause); break;
        case kEventNext: send_button(kButtonNext); break;
        case kEventPrevious: send_button(kButtonPrevious); break;
        default: break;
    }
}
@end

namespace {

BitChordScriptHandler* g_script_handler = nil;

constexpr AEEventID kScriptEvents[] = {kEventPlayPause, kEventPlay, kEventPause, kEventNext, kEventPrevious};

void start_on_main() {
    g_targets = [NSMutableArray array];
    MPRemoteCommandCenter* center = [MPRemoteCommandCenter sharedCommandCenter];
    add_target(center.playCommand, kButtonPlay);
    add_target(center.pauseCommand, kButtonPause);
    add_target(center.togglePlayPauseCommand, kButtonPlayPause);
    add_target(center.nextTrackCommand, kButtonNext);
    add_target(center.previousTrackCommand, kButtonPrevious);
    add_target(center.stopCommand, kButtonStop);
    center.changePlaybackPositionCommand.enabled = YES;
    id seek = [center.changePlaybackPositionCommand addTargetWithHandler:^MPRemoteCommandHandlerStatus(MPRemoteCommandEvent* event) {
        auto* change = static_cast<MPChangePlaybackPositionCommandEvent*>(event);
        send_seek(static_cast<jlong>(change.positionTime * 1000.0));
        return MPRemoteCommandHandlerStatusSuccess;
    }];
    [g_targets addObject:@[ center.changePlaybackPositionCommand, seek ]];

    g_script_handler = [BitChordScriptHandler new];
    NSAppleEventManager* events = [NSAppleEventManager sharedAppleEventManager];
    for (AEEventID id : kScriptEvents) {
        [events setEventHandler:g_script_handler
                    andSelector:@selector(handle:withReply:)
                  forEventClass:kSuite
                     andEventID:id];
    }
}

void stop_on_main() {
    for (NSArray* pair in g_targets) {
        MPRemoteCommand* command = pair[0];
        [command removeTarget:pair[1]];
        command.enabled = NO;
    }
    g_targets = nil;
    NSAppleEventManager* events = [NSAppleEventManager sharedAppleEventManager];
    for (AEEventID id : kScriptEvents) {
        [events removeEventHandlerForEventClass:kSuite andEventID:id];
    }
    g_script_handler = nil;
    g_generation++;
    g_art_url = nil;
    g_artwork = nil;
    MPNowPlayingInfoCenter* info = [MPNowPlayingInfoCenter defaultCenter];
    info.nowPlayingInfo = nil;
    info.playbackState = MPNowPlayingPlaybackStateStopped;
}

void fetch_artwork(NSString* url, NSUInteger generation) {
    NSURL* address = [NSURL URLWithString:url];
    if (address == nil) return;
    NSURLSessionDataTask* task = [[NSURLSession sharedSession]
          dataTaskWithURL:address
        completionHandler:^(NSData* data, NSURLResponse*, NSError* error) {
            if (error != nil || data == nil) return;
            NSImage* image = [[NSImage alloc] initWithData:data];
            if (image == nil) return;
            dispatch_async(dispatch_get_main_queue(), ^{
                if (generation != g_generation || g_targets == nil) return;
                g_artwork = [[MPMediaItemArtwork alloc] initWithBoundsSize:image.size
                                                            requestHandler:^NSImage*(CGSize) { return image; }];
                MPNowPlayingInfoCenter* info = [MPNowPlayingInfoCenter defaultCenter];
                NSMutableDictionary* current = [info.nowPlayingInfo mutableCopy];
                if (current == nil) return;
                current[MPMediaItemPropertyArtwork] = g_artwork;
                info.nowPlayingInfo = current;
            });
        }];
    [task resume];
}

}  // namespace

extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
    g_vm = vm;
    return JNI_VERSION_1_8;
}

JNIEXPORT jboolean JNICALL
Java_com_music_bitchord_desktop_DesktopMacMedia_nativeStart(JNIEnv* env, jobject) {
    // Looked up here, on a thread the JVM made, so the application's class
    // loader finds the class; the main queue's thread may not.
    jclass type = env->FindClass("com/music/bitchord/desktop/DesktopMacMedia");
    if (type == nullptr) {
        env->ExceptionClear();
        return JNI_FALSE;
    }
    g_on_button = env->GetStaticMethodID(type, "onButton", "(I)V");
    g_on_seek = env->GetStaticMethodID(type, "onSeek", "(J)V");
    if (g_on_button == nullptr || g_on_seek == nullptr) {
        env->ExceptionClear();
        env->DeleteLocalRef(type);
        return JNI_FALSE;
    }
    // Made once and kept: it is the same class every time, and a callback still
    // running from before a stop that timed out may be using it.
    if (g_type == nullptr) g_type = static_cast<jclass>(env->NewGlobalRef(type));
    env->DeleteLocalRef(type);
    dispatch_async(dispatch_get_main_queue(), ^{ start_on_main(); });
    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_com_music_bitchord_desktop_DesktopMacMedia_nativeStop(JNIEnv*, jobject) {
    // Waited for, so nothing is left pointing into the JVM once the player has
    // gone, but not forever: on quit this runs from a shutdown hook, and a
    // main thread that is itself waiting on the JVM would never get to it.
    if ([NSThread isMainThread]) {
        stop_on_main();
        return;
    }
    @autoreleasepool {
        dispatch_semaphore_t done = dispatch_semaphore_create(0);
        dispatch_async(dispatch_get_main_queue(), ^{
            stop_on_main();
            dispatch_semaphore_signal(done);
        });
        dispatch_semaphore_wait(done, dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC));
    }
}

JNIEXPORT void JNICALL
Java_com_music_bitchord_desktop_DesktopMacMedia_nativeUpdate(
    JNIEnv* env, jobject, jstring title, jstring artist, jstring album, jstring art_url,
    jboolean is_playing, jdouble rate, jlong position_ms, jlong duration_ms) {
    // JVM threads have no pool of their own, and the EDT never exits to drain one.
    @autoreleasepool {
        NSString* ns_title = to_ns(env, title);
        NSString* ns_artist = to_ns(env, artist);
        NSString* ns_album = to_ns(env, album);
        NSString* ns_art = to_ns(env, art_url);
        BOOL playing = is_playing == JNI_TRUE;
        double position = static_cast<double>(position_ms) / 1000.0;
        double duration = static_cast<double>(duration_ms) / 1000.0;
        double speed = rate > 0 ? static_cast<double>(rate) : 1.0;
        dispatch_async(dispatch_get_main_queue(), ^{
            if (g_targets == nil) return;
            MPNowPlayingInfoCenter* info = [MPNowPlayingInfoCenter defaultCenter];
            if (ns_title.length == 0) {
                g_generation++;
                g_art_url = nil;
                g_artwork = nil;
                info.nowPlayingInfo = nil;
                info.playbackState = MPNowPlayingPlaybackStateStopped;
                return;
            }
            if (![ns_art isEqualToString:g_art_url ?: @""]) {
                g_generation++;
                g_art_url = ns_art;
                g_artwork = nil;
                if (ns_art.length > 0) fetch_artwork(ns_art, g_generation);
            }
            NSMutableDictionary* now = [NSMutableDictionary dictionary];
            now[MPMediaItemPropertyTitle] = ns_title;
            now[MPMediaItemPropertyArtist] = ns_artist;
            now[MPMediaItemPropertyAlbumTitle] = ns_album;
            now[MPNowPlayingInfoPropertyMediaType] = @(MPNowPlayingInfoMediaTypeAudio);
            now[MPNowPlayingInfoPropertyElapsedPlaybackTime] = @(position);
            now[MPNowPlayingInfoPropertyPlaybackRate] = @(playing ? speed : 0.0);
            now[MPNowPlayingInfoPropertyDefaultPlaybackRate] = @(speed);
            if (duration > 0) now[MPMediaItemPropertyPlaybackDuration] = @(duration);
            if (g_artwork != nil) now[MPMediaItemPropertyArtwork] = g_artwork;
            info.nowPlayingInfo = now;
            info.playbackState = playing ? MPNowPlayingPlaybackStatePlaying : MPNowPlayingPlaybackStatePaused;
        });
    }
}

}  // extern "C"
