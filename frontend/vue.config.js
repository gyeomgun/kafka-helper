const path = require('path')
module.exports = {
    indexPath: 'templates/index.html',
    outputDir: path.resolve(__dirname, './target/dist'),
    assetsDir: 'static',
    devServer: {
        proxy: {
          '/api': {
            target: 'http://localhost:8081'
          }
    }
  }
};