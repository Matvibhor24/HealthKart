const mongoose = require('mongoose');

const doctorSchema = new mongoose.Schema({
    userId:{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    },
    name:{
        type: String,
        required: true
    },
    speciality:{
        type: String,
        required: true
    },
    address: {
        type: String,
        required: true
    },
    rating: {
        type: Number,
        default: 0
    },
    timings:[{
        type: String,
        required: true
    }]
});

module.exports = mongoose.model('Doctors',doctorSchema);