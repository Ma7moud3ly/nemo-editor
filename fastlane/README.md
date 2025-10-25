fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

### setup_env

```sh
[bundle exec] fastlane setup_env
```



----


## Android

### android build_apk

```sh
[bundle exec] fastlane android build_apk
```

Build APK (debug on branch, release on tag)

----


## desktop

### desktop linux

```sh
[bundle exec] fastlane desktop linux
```

Build Linux desktop binary

### desktop windows

```sh
[bundle exec] fastlane desktop windows
```

Build Windows desktop binary

### desktop macos

```sh
[bundle exec] fastlane desktop macos
```

Build macOS desktop binary

----


## wasm

### wasm build

```sh
[bundle exec] fastlane wasm build
```

Build wasm/js bundle

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
