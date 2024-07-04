var http = require('http');
const express = require('express');
const morgan = require('morgan');
const dotenv = require('dotenv');
const connectDB = require('./config/db');

dotenv.config({
    path: './config/config.env'
})
const port = process.env.PORT || 3000;
const host = "localhost";

const app = express();

connectDB();

// app.use((req,res,next)=>{
//     console.log("middleware ran");
//     req.title = "Mathur";
//     next();
// })
app.use(morgan('dev'));

app.use(express.json({}));
// app.use(express.json({
//     extended: true
// }));

app.use('/api',require('./routes/user'));
// app.post('/register',(req,res)=>{
//     res.json({
//         msg:'working'
//     });
// })
app.get('/auction',(req,res)=>{
    // res.statusCode = 200;
    // res.setHeader('Content-Type','text/json');
    // res.end('{"name":"vibhor"}');
    res.status(200).json({"name":"vibhor1"});
})
app.get('/',(req,res)=>{
    // res.statusCode = 200;
    // res.setHeader('Content-Type','text/json');
    // res.end('{"name":"vibhor2"}');
    res.status(200).json({"name":"vibhor3","Title":req.title});
})

app.listen(port,host,()=>{
    console.log(`Server started at http://${host}:${port}`);
})  
// const server = http.createServer((req,res)=>{
//     console.log(req.headers);

//     res.statusCode=200;
//     res.setHeader('Content-Type','text/html');
//     res.end('<html><body><h1>Success mil gayi</h1></body></html>');
// });

// server.listen(port,host,()=>{
//     console.log(`Server started at http://${host}:${port}`);
// })  