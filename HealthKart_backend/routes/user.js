const express = require('express');
const router = express.Router();
const User = require('../models/User');
const bcryptjs = require('bcryptjs');
const user_jwt = require('../middleware/user_jwt');
const jwt = require('jsonwebtoken');

router.get('/',user_jwt,async(req,res,next)=>{
    try{
        const user = await User.findById(req.user.id).select('-password');
        res.status(200).json({
            success:true,
            user: user
        });
    }catch(error){
        console.log(error.message);
        res.status(500).json({
            success:false,
            msg:'Server Error'
        });
        next();
    }
});
router.post('/register',async (req,res)=>{
    // res.status(200).json({
    //     msg:"working"
    // });
    // res.setHeader('Content-Type','application/json');
    // console.log(req.body);

    const {username,email,password,role} = req.body;
    let user_exist = await User.findOne({email:email});
    if (user_exist){
        return res.json({
            success: false,
            msg: 'User already exists'
        });
    }
    else{
        let user = new User();

        user.username = username;
        user.email = email;
        // user.password = password;
        const salt = await bcryptjs.genSalt(10);
        user.password = await bcryptjs.hash(password, salt);
        user.role = role;

        await user.save();

        const payload = {
            user:{
                id: user.id
            }
        }
        jwt.sign(payload,process.env.jwtUserSecret,{
            expiresIn: 360000
        }, (err,token)=>{
            if (err) throw err;
            res.status(200).json({
                success: true,
                token: token,
                role: user.role
            });
        });
        // res.json({
        //     success: true,
        //     msg: 'User registered',
        //     user: user
        // });
    }
});
router.post('/login', async (req, res) => {
    const { email, password } = req.body;

    try {
        let user = await User.findOne({ email });
        if (!user) {
            return res.status(400).json({ success: false, msg: "User not exists. Register to continue!" });
        }

        const isMatch = await bcryptjs.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({ success: false, msg: "Invalid Credentials" });
        }

        const payload = {
            user: {
                id: user.id
            }
        };

        jwt.sign(payload, process.env.jwtUserSecret, {
            expiresIn: 360000
        }, (err, token) => {
            if (err) throw err;
            res.status(200).json({
                success: true,
                token: token,
                role: user.role
            });
        });

    } catch (error) {
        console.error(error.message);
        return res.status(500).json({ 
            success: false,
            msg: "Server Error" 
        });
    }
});


module.exports = router;