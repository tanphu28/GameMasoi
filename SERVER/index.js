var express = require("express");

var app = express();
app.use(express.static("./public"));
app.set("view engine", "ejs");
app.set("views","./views");
var server = require("http").Server(app);
var io = require("socket.io")(server);
var mongoose = require("mongoose");
let User = require('./models/UserModel');
let Room = require('./models/RoomModel');
server.listen(3000);

io.on("connection",function(socket){
        //join room cho web test
        socket.join("TPv1zwikUlbUR7zp8lYZoRnPTWl1");
        socket.Phong="TPv1zwikUlbUR7zp8lYZoRnPTWl1";
        console.log("co nguoi ket noi den server");
        socket.on("disconnect",function(){
                console.log("ngat ket noi toi server");
        });
        

        //signup
        socket.on("register_user",function(data){
            console.log(data);
            var json = JSON.parse(data);
            var user = new User({
                id:json.id,
                username:json.username,
                name:json.username,
                id_room:""
            });
            user.save((err)=>{
                if(err)
                {
                    console.log("Them tat bai");
                }   
                else
                {
                    console.log("Them thanh cong");
                }
            });
        
        });

        //login
        socket.on("finduserlogin",function(data){
            User.findOne({id:data},function(err,doc)
            {
                if(err)
                {
                    console.log("Khong Tim Thay");
                }
                else
                {
                    console.log(doc);
                    socket.emit("userlogin",doc);
                    console.log("Tim thay!");
                }
            });
        });
        //all room
        socket.on("allroom",function()
        {
            Room.find(
                (err,doc)=>
                {
                    if(err)
                    {
                        console.log("that bai");
                    }
                    else
                    {
                        socket.emit("allroom",doc);
                        console.log("thanh cong");
                    }
                }
            );
        });
        //create room
        socket.on("createroom",function(data){
            var json =JSON.parse(data);
            var room = new Room({
                id : json.id,
                name:json.name,
                users : json.users,
                people : json.people,
                totalpeople: json.totalpeople,
                roomnumber:json.roomnumber,
                host:1
            });
            room.save((err)=>{
                if(err)
                {
                    console.log("Them tat bai");
                }   
                else
                {
                    socket.Phong="";
                    socket.Phong = room.id;
                    socket.join(room.id);
                    io.sockets.emit("newroom",room);
                    console.log(room);
                    console.log("Them thanh cong");
                }
            });
            console.log(data);
        });
        //joinroom
        socket.on("joinroom",function(data){
            var json  = JSON.parse(data);
            Room.findOne({id:json.id_room},function(err,doc){
                var user = new User(
                {
                    id:json.id,
                    name:json.name,
                    username:json.username,
                    id_room:json.id_room    
                }
                );
                doc.users.push(user);
                doc.people=doc.people+1;
                doc.save((err)=>{
                    if(err)
                    {
                        console.log("That Bai !");
                    }
                    else
                    {
                        socket.join(json.id_room);
                        socket.Phong=json.id_room;
                        io.sockets.in(json.id_room).emit("newuser",user);
                        console.log("Thanh Cong!");
                    }
                });
            });
        });
        //user ready
        socket.on("ready",function(data){
            io.sockets.in(socket.Phong).emit("ready",data);
        });

        //user exit
        socket.on("userexit",function(data){
            Room.findOne({id:socket.Phong},function(err,doc){

                if(err)
                {
                    console.log("That bai!");
                }
                else
                {
                    doc.people = doc.people-1;
                    console.log(doc);
                    doc.save((err)=>{
                        if(err)
                        {
                            console.log("That bai!");
                        }
                        else
                        {
                            console.log("thanh cong!");
                        }
                    });
                    if(doc.users.length==1)
                    {
                        Room.deleteOne({id:socket.Phong},function(err){
                            if(err)
                            {
                                console.log("That bai!");
                            }
                            else
                            {
                                console.log("Thanh cong!");
                                socket.leave(socket.Phong);
                            }
                        });
                        
                    }
                    else
                    {
                        Room.update(
                            {id:socket.Phong},
                            { $pull: { users: {id:data } }
                        },
                            { multi: true },function(err){
                                if(err)
                                {
                                    console.log("That Bai");
                                }
                                else
                                {
                                    console.log("Thanh cong !");
                                    io.sockets.in(socket.Phong).emit("userexit",data);
                                    socket.leave(socket.Phong);
                                   // socket.Phong="";
                                }
                            }
                        );
                    }
                   
                }
            });
            
            
        });
        //kick user
        
        socket.on("kickuser",function(data){
            io.sockets.in(socket.Phong).emit("leaveroom",data);
        });

        //Host
        socket.on("OK",function(data){
            io.sockets.in(socket.Phong).emit("OK",data);
        });
        socket.on("ListNhanVat",function(data){
            console.log(data);
            io.sockets.in(socket.Phong).emit("ListNhanVat",data);
        });
        socket.on("Luot",function(data){
            io.sockets.in(socket.Phong).emit("Luot",data);
        });
        socket.on("IDBiBoPhieu",function(data){
            io.sockets.in(socket.Phong).emit("IDBiBoPhieu",data);
        });
        socket.on("UserBoPhieu",function(data){
            io.sockets.in(socket.Phong).emit("UserBoPhieu",data);
        });
        socket.on("UserBoPhieuTat",function(data){
            io.sockets.in(socket.Phong).emit("UserBoPhieuTat",data);
        });
        socket.on("UserDie",function(data){
            io.sockets.in(socket.Phong).emit("UserDie",data);
        });
        socket.on("NhanVatsang",function(data){
            io.sockets.in(socket.Phong).emit("NhanVatsang",data);
        });
        socket.on("NhanVatTat",function(data){
            io.sockets.in(socket.Phong).emit("NhanVatTat",data);
        });
        socket.on("AllChat",function(data){
            io.sockets.in(socket.Phong).emit("AllChat",data);
        });
        socket.on("AllManHinhChon",function(data){
            io.sockets.in(socket.Phong).emit("AllManHinhChon",data);
        });
        socket.on("BangIdChon",function(data){
            var json = JSON.parse(data);
            io.sockets.in(socket.Phong).emit("BangIdChon",json.idchon);
        });
        socket.on("BangChonChucNang",function(data){
            console.log(data);
            var json = JSON.parse(data);
            
            io.sockets.in(socket.Phong).emit(json.manv,json.idchon);
        });
        socket.on("Chat",function(data){
            io.sockets.in(socket.Phong).emit("Chat",data);
        });
        socket.on("BangBoPhieu",function(data){
            console.log(data);
            io.sockets.in(socket.Phong).emit("BangBoPhieu",data);
        });
        socket.on("time",function(data){
            console.log(data);
            io.sockets.in(socket.Phong).emit("time",data);
        });
        socket.on("a",function(data){
            console.log(data);
            //io.sockets.in(socket.Phong).emit("BangBoPhieu",data);
        });
        socket.on("userhostexit",function(data){
            var id;
            Room.findOne({id:socket.Phong},function(err,doc){
                if(err)
                {
                    console.log("That bai!");
                }
                else
                {
                    doc.people = doc.people-1;
                    doc.host=0;
                    
                    console.log(doc);
                    doc.save((err)=>{
                        if(err)
                        {
                            console.log("That bai!");
                        }
                        else
                        {
                            id = doc.users[0].id;
                            console.log("thanh cong!");
                        }
                    });
                    if(doc.users.length==1)
                    {
                        Room.deleteOne({id:socket.Phong},function(err){
                            if(err)
                            {
                                console.log("That bai!");
                            }
                            else
                            {
                                console.log("Thanh cong!");
                                socket.leave(socket.Phong);
                                
                            }
                        });
                        
                        
                    }
                    else
                    {
                        Room.update(
                            {id:socket.Phong},
                            { $pull: { users: {id:data } }
                        },{ multi: true },function(err){
                                if(err)
                                {
                                    console.log("That Bai");
                                }
                                else
                                {
                                    console.log("Thanh cong !");
                                    io.sockets.in(socket.Phong).emit("userexit",data);
                                    io.sockets.in(socket.Phong).emit("useruphost",id);
                                    socket.leave(socket.Phong);
                                   // socket.Phong="";
                                }
                            }
                        );
                    }
                    
                    
                }
            });
        });

        // socket.on(
        // "click",function(data)
        // {
        //     var json = JSON.parse(data);
        //     console.log(json.fullname);
        //     var sv = new SinhVien();
        //     // sv.ID=json.ID;
        //     // sv.username = json.username;
        //     // sv.pass = json.pass;
        //     // sv.fullname = json.fullname;
        //     // sv.save((err)=>{
        //     //         if(err)
        //     //         {
        //     //             console.log("Them that bai");
        //     //         }
        //     //         else
        //     //         {
        //     //             console.log("Them thanh cong");
        //     //         }
        //     //     });
        //     // SinhVien.findOne({ID:json.ID},function(err,doc){
        //     //     if(err)
        //     //     {
        //     //         console.log("Khong Tim Thay");
        //     //     }
        //     //     else
        //     //     {
        //     //         console.log(doc);
        //     //         socket.emit("newclick",doc);
        //     //     }
        //     // });

        //     // var room = new Room();
        //     // Room.findOne({name:"Phong 1"},function(err,doc){
        //     //     if(err)
        //     //     {
        //     //         console.log("Khong tim thay !!");
        //     //     }
        //     //     else
        //     //     {
        //     //         room =doc;
        //     //         socket.emit("newclick",room);
        //     //     }
                
            
        //     // });
            
        // });
});
//dug tu server trarve tat ca moi nguoi io.sockets.emit
//tra ve thang vua gui len su dung socket.emit
//khong tra ve a gui may thang xung quanh socket.broadcast.emit
//

app.get("/",function(req,res){
    res.render("index");
});
let options ={
    db:{native_parser:true},
    server: {poolSize: 5},
    user:'admin',
    pass:'admin'
};
mongoose.Promise = global.Promise;
mongoose.connect('mongodb://localhost:27017/MasoiDB',options).then(
    ()=>{
        console.log("connect Db Succes");
    },
    err =>{
        console.log("connect db fail");
    }


);

// Room.find({id:"vAeGwFOYfUOOJNxeltsYSWqbOll2"},function(err,doc)
// {
//     if(err)
//     {
//         console.log("khong tim thay phong!");
//     }
//     else
//     {
//         // doc.users.remove({id:"TPv1zwikUlbUR7zp8lYZoRnPTWl1"});
//         // doc.save((err)=>{
//         //     if(err)
//         //     {
//         //         console.log("Thanh cong !");
//         //     }
//         //     else
//         //     {
//         //         console.log("That bai!");
//         //     }
//         // });
//         doc.update(
//             {},
//             { $pull: { users: { $elemMatch: { id:"TPv1zwikUlbUR7zp8lYZoRnPTWl1" } } } },
//             { multi: true }
//             );
//         console.log("Thanh cong!");
//     }
// });


// var user2 = new Persson({
//     name:"user1",
//     fullname : "Dang Tan Phu"
// });
// user2.save((err)=>{
//     if(err)
//     {
//         console.log("Them that bai");
//     }
//     else
//     {
//         console.log("Them thanh cong");
//     }
// });

// Persson.findOne({name:"user1"},function(err,doc){
//         if(err)
//         {
//             console.log("Khong tim thay !!");
//         }
//         else
//         {
//             doc.fullname = "aaaaaaaaa";
//             doc.save((er)=>{
//                 if(er)
//                 {
//                     console.log("Edit that bai");

//                 }
//                 else{
//                     console.log("Edit thanh cong")
//                 }
                
//             });
//         }
//         console.log(doc);

// });
// var pe = Persson.findOne({name:"user1"}).exec();
// console.log(pe);
// var user = new Persson();
// Persson.findOne({name:"user1"},function(err,doc){
//             if(err)
//             {
//                 console.log("Khong tim thay !!");
//             }
//             else
//             {
//                 user = doc;
//                 Room.findOne({name:"Phong 1"},function(err,doc){
//                     if(err)
//                     {
//                         console.log("Khong tim thay !!");
//                     }
//                     else
//                     {
//                         doc.users.push(user);
//                         doc.save((err)=>{
                
//                             if(err)
//                             {
//                                 console.log(" that bai");
//                             }
//                             else
//                             {
//                                 console.log("Thanh cong");
//                             }
//                         });
//                         console.log(doc);
//                     }
                    
                
//                 });
//             }
//             console.log(user);

//     });
//     console.log(user);
// var room = new Room({
//     name : "Phong 1",
// });
// room.users.push(user);
// room.save((err)=>{
//         if(err)
//         {
//             console.log("Them that bai");
//         }
//         else
//         {
//             console.log("Them thanh cong");
//         }
//     });
// Room.findOne({name:"phong 1"},function(err,doc){
//     if(err)
//     {
//         console.log("Khong tim thay !!");
//     }
//     else
//     {
//         doc.users.push(user);
//         doc.save((err)=>{

//             if(err)
//             {
//                 console.log(" that bai");
//             }
//             else
//             {
//                 console.log("Thanh cong");
//             }
//         });
//     }
//     console.log(doc);

// });

