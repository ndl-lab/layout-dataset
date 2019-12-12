var merge = require("webpack-merge"); // webpack-merge
var common = require("./webpack.common.js"); // 汎用設定をインポート
var webpack = require("webpack");

module.exports = merge(common, {
  mode: "development",
  output: {
    path: __dirname + "/dst/assets/js/",
    publicPath: "/assets/js/",
    filename: "[name].bundle.js"
  },
  devServer: {
    contentBase: __dirname + "/dst/",
    host: "0.0.0.0",
    disableHostCheck: true,
    proxy: [
      {
        context: ["/api/**"],
        target: "http://localhost:9985/"
      }
    ],
    historyApiFallback: {
      index: "/index.html"
    }
  },
  plugins: [
    new webpack.DefinePlugin({
      DEFINE_BASE_FULL_PATH: "'/'"
    })
  ],
  devtool: "inline-source-map"
});
