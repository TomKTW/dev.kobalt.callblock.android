CallBlock
===========

This application allows you to block incoming calls from specific phone numbers.

## Features

- Block or warn about incoming calls through screening them
- Use predefined rules for screening calls
- Use option to allow incoming calls from contacts only.
- Use user defined rules for defining your own ruleset and if needed to override predefined or
  contact rules
- Permission option variety in case additional permissions are required
- Night mode theme available

## Requirements

- Android 5.0+

### Permissions

Note: Depending on Android version, you may require either call screening role, default dialer or
additional permissions.

- Call screening role: To perform call screening on Android 10.0+, you need to give a role to this
  application.
- Default dialer: To check incoming calls on Android 7.0+, this application should be set as default
  dialer.
- Read phone state: This permission is required to retrieve incoming calls.
- Read call logs: This permission is required to retrieve phone number from incoming calls.
- Read contacts: This permission is required to check if phone number from incoming call exists in
  contacts.
- Call phone: This permission is required to end incoming call.
- Answer phone calls: This permission is required to drop incoming call on Android 8.0+.

## Third party components

- [Kotlin programming language](https://kotlinlang.org) - Apache-2.0
- [Android Jetpack Libraries](https://developer.android.com/jetpack) - Apache-2.0
- [Google Material Design Components](https://github.com/material-components/material-components-android)
  - Apache-2.0
- [LibPhoneNumber-Android Phone Number Utility](https://github.com/MichaelRocks/libphonenumber-android)
  - Apache-2.0
- [ShapeOfView Shape Layouts](https://github.com/florent37/ShapeOfView) - Apache-2.0
- [Simple Stack Navigation](https://github.com/Zhuinden/simple-stack) - Apache-2.0
- [Phone Input Field](https://github.com/lamudi-gmbh/android-phone-field) - Apache-2.0