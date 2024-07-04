const jwt = require('jsonwebtoken');

const check = async (req,res,next)=>{
    const token = req.header('Authorization');

    if (!token){
        res.status(401).json({
            msg:'No token, authorization denied'
        })
    }
    else{
        // try{
            await jwt.verify(token,process.env.jwtUserSecret,(err,decoded)=>{
                if (err){
                    res.status(401).json({
                        msg:'Token not valid'
                    })
                }
                else{
                    req.user = decoded.user;
                    next();
                }
            });
        // }
        // catch(err){
        //     console.log(err);
        //     res.status(500)/json({
        //         msg:'Server error'
        //     });
        // }
    }
}
module.exports = check;