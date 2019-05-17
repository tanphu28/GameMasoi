var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var PersonSchema  = new Schema({

    id:{
        type:String
    },

    username:{
        type:String,
        require : true
    },
    name:{
        type:String,
        default:""
    },
    id_room:{
        type:String,
        default:""
    },
    create_date:{
        type:Date,
        default:Date.now
    }
    
});
// UserSchema.path('name').set((inputString)=>{


// });
module.exports = mongoose.model('User',PersonSchema);
