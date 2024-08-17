const express = require ('express')
const Doctor = require('../models/Doctors.js')
const auth = require('../middleware/user_jwt');
const router = express.Router();

router.get('/all',async (req,res,next)=>{
    try {
        const doctorList = await Doctor.find();
        res.status(200).json({
            count: doctorList.length,
            alldoctors: doctorList
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            msg:"Server error"
        });
        next();
    }
});

router.get('/exists',auth,async(req,res,next)=>{
    try {
        let doctor = await Doctor.findOne({userId:req.user.id});
        if (!doctor){
            return res.status(200).json({
                success:false,
                msg: "Doctor Info not entered"
            });
        }
        else{
            return res.status(200).json({
                success:true,
                doctorInfo: doctor
            });
        }
    } catch (error) {
        console.log(error.message);
        res.status(500).json({
            success:false,
            msg:'Server Error'
        });
        next();
    }
});

router.post('/add',auth,async(req,res)=>{
    try {
        const doctor = await Doctor.create({
            userId: req.user.id,
            name: req.body.name,
            speciality: req.body.speciality,
            contactNo: req.body.contactNo,
            address: req.body.address,
            timings:req.body.timings
        });
        if (!doctor){
            return res.status(400).json({
                success: false,
                msg: "Something went wrong"
            });
        }
        return res.status(200).json({
            success: true,
            msg: "Successfully created"
        });
    } catch (error) {
        next(error);
    }
});

router.put('/:id',async (req,res,next)=>{
    try {
        var doctor = await Doctor.findById(req.params.id);
        if (!doctor){
            return res.status(400).json({
                success: false,
                msg: "Doctor not exists"
            });
        }
        doctor = await Doctor.findByIdAndUpdate(req.params.id, req.body, {
            new: true,
            runValidators: true
        });
        if (!doctor){
            return res.status(400).json({
                success: false,
                msg: "Something went wrong"
            });
        }
        return res.status(200).json({
            success: true,
            msg: "Successfully updated",
            doctorInfo: doctor
        });
    } catch (error) {
        next(error);
    }
})

module.exports = router;