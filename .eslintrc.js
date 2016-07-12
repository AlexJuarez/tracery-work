module.exports = {
    "extends": "airbnb",
    "installedESLint": true,
    "parser": "babel-eslint",
    "plugins": [
        "import",
        "flowtype",
        "flow-vars",
        "react"
    ],
    "rules": {
      // We use Flow, which does a better job than this linter at detecting when
      // something has not been defined.
      "no-use-before-define": "off",

      // AirBnB's style rule takes an opinionated stance on leading underscores as a convention for
      // indicating privacy of class members, recommending instead that various language hacks like
      // WeakRef or the Crockford privacy pattern be used. We find that the extra assurance is not
      // worth the extra typing.
      "no-underscore-dangle": "off",

      "flowtype/require-parameter-type": ["error", "always"],
      "flowtype/require-return-type": ["error", "always"],
      "flowtype/require-valid-file-annotation": ["error", "always"],
      "flowtype/space-after-type-colon": ["error", "always"],
      "flowtype/space-before-type-colon": ["error", "never"],
      "flowtype/type-id-match": ["error", "^([A-Z][a-z0-9]+)+$"],

      // These aren't so much rules as transforms so that the rest of ESLint gets out of the way
      "flow-vars/define-flow-type": 1,
      "flow-vars/use-flow-type": 1,

      // The import plugin handles Flow `import type` syntax, whereas ESLint itself does not.
      "no-duplicate-imports": "off",
      "import/no-duplicates": "error",
    },
};
