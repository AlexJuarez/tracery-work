[![Build Status](https://travis-ci.com/facebookincubator/tracery-prerelease.svg?token=7sCGrGNQ9WPN1jgBCXfJ&branch=master)](https://travis-ci.com/facebookincubator/tracery-prerelease)

## Setup
### Once

```
brew install node
node install -g npm
```

### On any rebase

```
npm install
```

### To use locally

1. ```npm start```
2. Open `localhost:8080` in Chrome
3. Many types of edits will automatically be applied thanks to React Hot Loader

### To test

```
npm run jest
```

Will run Jest tests.

```
npm run coverage
```

Will run Jest tests while recording coverage. Find the report at `coverage/lcov-report/index.html`.

```
npm test
```

Will run Flow typechecking, then Jest tests, then ESLint style checking.
