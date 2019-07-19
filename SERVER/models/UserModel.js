var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var PersonSchema  = new Schema({

    userId:{
        type:String
    },

    fullname:{
        type:String,
        default:""
    },
    name:{
        type:String,
        default:""
    },
    phone_number : {
        type : String,
        default:""
    },
    birthday : {
        type : String,
        default:""
    },
    address : {
        type : String,
        default:""
    },
    email : {
        type : String,
        default:""
    },
    level : {
        type : Number,
        default:1
    },
    win : {
        type : Number,
        default:0
    },
    lose : {
        type : Number,
        default:0
    },
    cancle : {
        type : Number,
        default:0
    },
    money : {
        type : Number,
        default:0
    },
    id_room:{
        type:String,
        default:""
    },
    isActive : {
        type:Boolean,
        default:false
    },
    create_date:{
        type:Date,
        default:Date.now
    }
    
});
module.exports = mongoose.model('User',PersonSchema);
