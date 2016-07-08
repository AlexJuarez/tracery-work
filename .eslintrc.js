module.exports = {
    "extends": "airbnb",
    "installedESLint": true,
    "parser": "babel-eslint",
    "plugins": [
        "flowtype",
        "flow-vars",
        "react"
    ],
    "rules": {
      // We use Flow, which does a better job than this linter at detecting when
      // something has not been defined.
      "no-use-before-define": "off",

      "flowtype/require-parameter-type": [2, "always"],
      "flowtype/require-return-type": [2, "always"],
      "flowtype/require-valid-file-annotation": [2, "always"],
      "flowtype/space-after-type-colon": [2, "always"],
      "flowtype/space-before-type-colon": [2, "never"],
      "flowtype/type-id-match": [2, "^([A-Z][a-z0-9]+)+$"],

      "flow-vars/define-flow-type": 1,
      "flow-vars/use-flow-type": 1,
    },
};
