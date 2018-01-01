"use strict";

const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

const extractSass = new ExtractTextPlugin({
    filename: "../stylesheets/[name].css",
    disable: process.env.NODE_ENV === "development"
});

module.exports = {
    entry: {
        main: './front/assets/javascripts/main.js'
    },
    output: {
        path: path.resolve(__dirname, 'public/javascripts'),
        publicPath: 'assets/javascripts/',
        filename: '[name].bundle.js'
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                use: 'babel-loader'
            },
            {
                test: /\.scss$|.css$/,
                use: extractSass.extract({
                    use: [
                        {loader: 'css-loader'},
                        {loader: 'resolve-url-loader'},
                        {loader: 'sass-loader', options: {sourceMap: true}}
                    ],
                    fallback: 'style-loader'
                })
            },
            {
                test: /\.(eot|svg|ttf|woff|woff2|jpg|gif)$/,
                use: [
                    {
                        loader: 'file-loader',
                        options: process.env.NODE_ENV !== 'development' ? {
                                name: '../stylesheets/[name].[ext]',
                                publicPath: './'
                            } : {}
                    }
                ]
            }
        ]
    },
    plugins: [extractSass],
    devtool: 'eval',
    devServer: {
        public: 'anthonysyk.ddns.net:8080',
        host:'192.168.1.26',
        port: '8080',
        compress: true,
        proxy: {
            '*': {
                target: 'http://192.168.1.26:9000'
            }
        }
    }


};