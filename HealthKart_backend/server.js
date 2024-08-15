var http = require('http');
const express = require('express');
const morgan = require('morgan');
const dotenv = require('dotenv');
const connectDB = require('./config/db');

dotenv.config({
    path: './config/config.env'
})
const port = process.env.PORT || 3000;
const host = "0.0.0.0";

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

app.use('/api/auth',require('./routes/user.js'));
// app.post('/register',(req,res)=>{
//     res.json({
//         msg:'working'
//     });
// })
app.use('/api/doctors',require('./routes/doctor.js'));

app.listen(port,host,()=>{
    console.log(`Server started at http://${host}:${port}`);
})  
