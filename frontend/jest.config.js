module.exports = {
    preset: 'jest-expo',
    transformIgnorePatterns: [
        'node_modules/(?!((jest-)?react-native|@react-native(-community)?)|expo(nent)?|@expo(nent)?/.*|@expo-google-fonts/.*|react-navigation|@react-navigation/.*|@unimodules/.*|unimodules|sentry-expo|native-base|react-native-svg|react-native-toast-message)',
    ],
    setupFilesAfterSetup: ['@testing-library/jest-native/extend-expect'],
    collectCoverageFrom: [
        'src/**/*.{js,jsx}',
        '!src/**/index.js',
        '!**/node_modules/**',
    ],
    moduleFileExtensions: ['js', 'jsx', 'json', 'node'],
};
