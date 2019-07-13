var mongoose = require('mongoose');
var User = require('../models/UserModel').schema;
var Schema = mongoose.Schema;
var RoomSchema  = new Schema({
    id:{
        type:String
    },
    
    name:{
        type:String
    },
    users:{
        type: [User]
    },

    people:{
        type:Number
    },

    totalpeople:{
        type:Number
    },
    roomnumber:{
        type:Number
    },
    host:{
        type:Number
    },

    money:{
        type:Number
    },
    create_date:{
        type:Date,
        default:Date.now
    }
    
});
// UserSchema.path('name').set((inputString)=>{


// });
module.exports = mongoose.model('RoomReal',RoomSchema);
