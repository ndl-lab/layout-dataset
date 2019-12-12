const webpack = require("webpack");
const VueLoaderPlugin = require("vue-loader/lib/plugin");

module.exports = {
  context: __dirname + "/src",
  entry: {
    app: "./main.ts"
  },
  optimization: {
    moduleIds: "named"
  },
  resolve: {
    extensions: [".ts", ".js", ".vue"],
    alias: {
      vue: "vue/dist/vue.common.js",
      "vue-class-component":
        "vue-class-component/dist/vue-class-component.common.js"
    },
    modules: ["./src", "node_modules"]
  },
  module: {
    rules: [
      {
        test: /\.vue$/,
        exclude: /node_modules/,
        loader: "vue-loader"
      },
      {
        test: /\.html$/,
        use: [
          {
            loader: "html-loader",
            options: {
              minimize: true
            }
          }
        ]
      },
      {
        test: /\.(sass|scss)$/,
        use: [
          "vue-style-loader",
          "css-loader",
          {
            loader: "sass-loader",
            options: {
              includePaths: ["src", "src/styles", "node_modules"]
            }
          }
        ]
      },
      {
        test: /\.css$/, //Check for sass or scss file names
        use: ["style-loader", "css-loader"]
      },
      {
        test: /\.ts$/,
        loader: "ts-loader",
        options: { appendTsSuffixTo: [/\.vue$/] }
      },
      {
        test: /\.(jpg|png|svg)$/,
        loaders: "file-loader?name=[name].[ext]"
      }
    ]
  },
  plugins: [new VueLoaderPlugin(), new webpack.ProvidePlugin({})]
};
