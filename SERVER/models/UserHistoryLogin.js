var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var UserHistory  = new Schema({
    userId:{
        type : String
    },
    login_dt : {
        type:Date,
        default:Date.now
    }, 
});
module.exports = mongoose.model('UserHistory',UserHistory);
