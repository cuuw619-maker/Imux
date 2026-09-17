#include "core/logger.h"

#include <android/log.h>

namespace imux::core {

namespace {

int androidPriority(LogLevel level) {
    switch (level) {
        case LogLevel::Info: return ANDROID_LOG_INFO;
        case LogLevel::Warning: return ANDROID_LOG_WARN;
        case LogLevel::Error: return ANDROID_LOG_ERROR;
    }
    return ANDROID_LOG_INFO;
}

} // namespace

void log(LogLevel level, std::string_view message) {
    __android_log_print(androidPriority(level), "Imux/Core", "%.*s", static_cast<int>(message.size()), message.data());
}

} // namespace imux::core
