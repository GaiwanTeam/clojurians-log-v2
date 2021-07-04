module.exports = {
  mode: 'jit',
  purge: [
    "src/**/*.clj", 
    "src/**/*.cljc",
    "src/**/*.cljs"
  ],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {},
  },
  variants: {
    extend: {},
  },
  plugins: [],
}
