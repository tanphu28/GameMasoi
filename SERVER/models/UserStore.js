var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var UserStore  = new Schema({
    userId:{
        type:String
    },
    
    passWord:{
        type:String
    },

    create_date:{
        type:Date,
        default:Date.now
    }
    
});
module.exports = mongoose.model('UserStore',UserStore);
