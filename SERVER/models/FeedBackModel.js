var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var FeedBack  = new Schema({
    email:{
        type:String
    },
    
    message:{
        type:String
    },

    create_date:{
        type:Date,
        default:Date.now
    }
    
});
module.exports = mongoose.model('FeedBack',FeedBack);