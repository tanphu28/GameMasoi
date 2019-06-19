var mongoose = require('mongoose');
var User = require('../models/UserModel').schema;
var Schema = mongoose.Schema;
var UserFriends=new Schema({
    friend_no:{
        type:String
    },
    userId1:{
        type:String
    },
    userId2:{
        type:String
    },
    users:{
        type: [User]
    },
    regist_dt : {
        type:Date,
        default:Date.now
    }, 

}) 
module.exports=mongoose.model("UserFriends",UserFriends);
