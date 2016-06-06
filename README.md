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
npm test
```

Will run tests.

```
npm run coverage
```

Will run tests while recording coverage. Find the report at `coverage/lcov-report/index.html`.

```
npm run test-all
```

Will run tests, Flow typechecking, and ESLint style checking.
