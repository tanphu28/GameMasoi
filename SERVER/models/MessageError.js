var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var MessageError  = new Schema({
    message:{
        type:String
    },

    create_date:{
        type:Date,
        default:Date.now
    }
    
});
module.exports = mongoose.model('MessageError',MessageError);