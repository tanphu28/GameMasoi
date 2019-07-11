var express = require("express");

var app = express();
app.use(express.static("./public"));
app.set("view engine", "ejs");
app.set("views", "./views");
var server = require("http").Server(app);
var io = require("socket.io")(server);
var mongoose = require("mongoose");
let User = require('./models/UserModel');
let Room = require('./models/RoomModel');
let UserStore = require('./models/UserStore');
let UserFriends = require('./models/UserFriends');
let FeedBack = require('./models/FeedBackModel');
let RoomCache = require('./models/RoomCache');
let UserHistory = require('./models/UserHistoryLogin');
var PORT = process.env.PORT || 3000
const versionName = '1.0'
server.listen(PORT);
var fs = require('fs');
var http = require('http');
var path = require('path');
var roomarr = new Array();
http.createServer(function (req, res) {
    if (req.url.match(/.jpg$/)) {
        var imgPath = path.join(__dirname, 'image', req.url);
        var imgStream = fs.createReadStream(imgPath);
        res.writeHead(200, { "Content-Type": "image/jpeg" });
        imgStream.pipe(res);
    }
}).listen(4000);
var room = new RoomCache();
room._id="bb";
roomarr["aa"] = room;
roomarr["aa"]._id="1";
roomarr["aa"].idBOPHIEU="2";
roomarr["aa"].idUserDie="3";
roomarr["aa"].idBaoVeChon="4";
roomarr["aa"].idThoSanChon="5";
for(let i=0;i<7;i++){
    roomarr["aa"].arrAll.push(i);
    roomarr["aa"].arrMaSoiChon.push(i);
    roomarr["aa"].arrBoPhieu.push(i);
    roomarr["aa"].arrKetQuaBoPhieu.push(i);
    roomarr["aa"].arrReady.push(i);
    roomarr["aa"].arrUserDie.push(i);
}
io.on("connection", function (socket) {
    // socket.on("room",function(){
    //     socket.emit("room",roomarr["aa"].arrAll);
    // });
    // socket.on("rs",function(){
    //     roomarr["aa"] = new RoomCache();
    //     roomarr["aa"].arrAll.push("1");
    // })
    //join room cho web test
    socket.Phong="";
    socket.host=0;
    socket.userId="";
    socket.UserFriends = "TPv1zwikUlbUR7zp8lYZoRnPTWl1";
    console.log("co nguoi ket noi den server");
    socket.on("disconnect", function () {
        console.log("ngat ket noi toi server");
        if(socket.Phong!=""){
            Room.findOne({ _id: socket.Phong }, function (err, doc) {
                if (err) {
                    console.log("That bai! 0");
                }
                else {
                    doc.people = doc.users.length - 1;
                    doc.host = 0;
    
                    console.log(doc);
                    doc.save((err) => {
                        if (err) {
                            console.log("That bai! 1");
                        }
                        else {
                            console.log("thanh cong!");
                            if (doc.users.length == 1) {
                                Room.deleteOne({ _id: socket.Phong }, function (err) {
                                    if (err) {
                                        console.log("That bai! 2");
                                    }
                                    else {
                                        console.log("Thanh cong!");
                                        io.sockets.emit("DeleteRoom", socket.Phong);
                                        socket.leave(socket.Phong);
    
    
                                    }
                                });
    
    
                            }
                            else {
                                id = doc.users[1].userId;
                                Room.update(
                                    { _id: socket.Phong },
                                    {
                                        $pull: { users: { userId: socket.userId } }
                                    }, { multi: true }, function (err) {
                                        if (err) {
                                            console.log("That Bai 3");
                                        }
                                        else {
                                            console.log("Thanh cong !");
                                            io.sockets.in(socket.Phong).emit("userexit",  socket.userId);
                                            if(socket.host==1){
                                                io.sockets.in(socket.Phong).emit("useruphost", id);
                                                socket.host=0;
                                            }
                                            console.log(id);
                                            socket.leave(socket.Phong);
                                            socket.Phong="";
                                        }
                                    }
                                );
                            }
                        }
                    });
    
    
    
                }
            });
        }
    });

    //update version android
    socket.on("CheckVersionName", function (data) {
        console.log(data);
        socket.emit("CheckVersionName", versionName);
    })
    socket.emit("updateversion", versionName);


    //signup
    socket.on("register_user", function (data) {
        // console.log(data);
        // var json = JSON.parse(data);
        // var user = new User({
        //     id:json.id,
        //     username:json.username,
        //     name:json.username,
        //     id_room:""
        // });
        // user.save((err)=>{
        //     if(err)
        //     {
        //         console.log("Them tat bai");
        //     }   
        //     else
        //     {
        //         console.log("Them thanh cong");
        //     }
        // });
        console.log(data);
        var json = JSON.parse(data);
        UserStore.findOne({ userId: json.userId }, function (err, data) {
            if (err) {
                console.log('Khong Tim thay');
            }
            else {
                if (data == null) {
                    var userStore = new UserStore({
                        userId: json.userId,
                        passWord: json.passWord
                    })
                    userStore.save((err) => {
                        socket.emit('register_user', true);
                        socket.userId=json.userId;
                    })
                }
                else {
                    socket.emit('register_user', false);
                }
            }
        })

    });

    socket.on("CheckUser", function (data) {
        User.findOne({ name: data }, (err, data) => {
            if (err) {
                console.log('Khongtim Thay!');
            }
            else {
                if (data == null) {
                    socket.emit('CheckUser', true);
                }
                else {
                    socket.emit('CheckUser', false);
                }
            }
        })
    })

    socket.on("Registnickname", function (data) {
        var json = JSON.parse(data);
        var user = new User({
            userId: json.userId,
            name: json.name
        })
        user.save((err) => {
            if (err) {
                console.log("Err");
            }
            else {
                console.log("Thanh Cong")
                User.findOne({ userId: json.userId }, (err, doc) => {
                    if (err) {
                        console.log('That Bai');
                    }
                    else {
                        console.log("Thanh cong");
                        console.log(doc);
                        socket.emit("Registnickname", doc);
                        let userHistory = new UserHistory({
                            userId : json.userId
                        });
                        userHistory.save();
                    }
                })
            }

        })
    })

    //logginFB
    socket.on("LoginFB", function (data) {
        var json = JSON.parse(data);
        User.findOne({ userId: json.id }, (err, data) => {
            if (data == null) {
                var user = new User({
                    userId: json.id,
                    fullname: json.name
                })
                user.save((err) => {
                    if (err) {
                        console.log("That Bai");
                    }
                    else {
                        console.log("Thanh Cong");
                        socket.emit('register_user', true);
                        socket.userId=json.id;
                    }
                })
            }
            else {
                socket.emit("LonginSuccess", data);
                let userHistory = new UserHistory({
                    userId : json.id
                });
                userHistory.save();
            }
        })
    })

    socket.on("RegistnicknameLoginFb", function (data) {
        var json = JSON.parse(data);
        var user = new User({
            userId: json.userId,
            name: json.name
        });
        console.log("Thanh Cong");
        User.findOne({ userId: json.userId }, (err, doc) => {
            if (err) {
                console.log('That Bai');
            }
            else {
                doc.name = json.name;
                doc.save((err) => {
                    console.log("Thanh cong");
                    console.log(doc);
                    socket.emit("Registnickname", doc);
                    let userHistory =new UserHistory({
                        userId : json.userId
                    });
                    userHistory.save();
                })

            }
        })
    })
    //login
    socket.on("login", function (data) {
        var json = JSON.parse(data)
        UserStore.findOne({ userId: json.userId, passWord: json.passWord }, function (err, doc) {
            if (err) {
                console.log("Khong Tim Thay");
            }
            else {
                if (doc == null) {
                    socket.emit("userlogin", null);
                }
                else {
                    User.findOne({ userId: json.userId }, function (err, doc2) {
                        if (err) {
                            console.log('That Bai')
                        }
                        else {
                            socket.emit("userlogin", doc2);
                            socket.userId=json.userId;
                            console.log(doc2)
                            console.log("Thanh cong");
                            let userHistory = new UserHistory({
                                userId : json.userId
                            });
                            userHistory.save();
                        }
                    })
                }
            }
        });
    });
    //all room
    socket.on("allroom", function (data) {
        Room.find(
            (err, doc) => {
                if (err) {
                    console.log("that bai");
                }
                else {
                    socket.emit("allroom", doc);
                    console.log("thanh cong");
                }
            }
        );
        //socket.emit("allroom", roomarr);
    });

    //create room
    socket.on("createroom", function (data) {
        var json = JSON.parse(data);
        var room = new Room({
            id: json.id,
            name: json.name,
            users: json.users,
            people: json.people,
            totalpeople: json.totalpeople,
            roomnumber: json.roomnumber,
            money:json.money,
            host: 1
        });
        room.save((err) => {
            if (err) {
                console.log("Them tat bai");
            }
            else {
                Room.findOne({ id: json.id }, function (err, data) {
                    if (err) {
                        onsole.log("that bai");
                    }
                    else {
                        socket.host=1;
                        socket.Phong = "";
                        socket.Phong = data._id;
                        socket.join(data._id);
                        io.sockets.emit("newroom", data);
                        console.log(socket.Phong);
                        console.log("Them thanh cong");
                        var room = new RoomCache();
                        room._id=socket.Phong;
                        roomarr[socket.Phong] =room; 
                    }
                });

            }
        });
        console.log(data);
    });
    //joinroom
    socket.on("joinroom", function (data) {
        var json = JSON.parse(data);
        Room.findOne({ _id: json.id_room }, function (err, doc) {
            if(err){
                console.log("That bai");
            }else{
                if (doc.users.length == 7) {
                    var response = {
                        flag: true,
                        room: doc
                    }
                    socket.emit("FullPeople", response);
                }
                else {
                    var response = {
                        flag: false,
                        room: doc
                    }
                    socket.emit("FullPeople", response);
                    var user = new User(
                        {
                            userId: json.userId,
                            name: json.name,
                            fullname: json.fullname,
                            id_room: json.id_room
                        }
                    );
                    doc.users.push(user);
                    doc.people = doc.people + 1;
                    doc.save((err) => {
                        if (err) {
                            console.log("That Bai !");
                        }
                        else {
                            socket.host=0;
                            socket.join(json.id_room);
                            socket.Phong = json.id_room;
                            io.sockets.in(json.id_room).emit("newuser", user);
                            console.log(socket.Phong);
                            console.log("Thanh Cong!");
                        }
                    });
                }
            }

        });
        // roomarr.forEach(element => {
        //     if (element._id == json.id_room) {
        //         if (element.users.length == 7) {
        //             var response = {
        //                 flag: true,
        //                 room: element
        //             }
        //             socket.emit("FullPeople", response);
        //         }
        //         else {
        //             var response = {
        //                 flag: false,
        //                 room: element
        //             }
        //             socket.emit("FullPeople", response);
        //             var user = new User(
        //                 {
        //                     userId: json.userId,
        //                     name: json.name,
        //                     fullname: json.fullname,
        //                     id_room: json.id_room
        //                 }
        //             );
        //             element.users.push(user);
        //             element.people = element.people + 1;
        //             socket.join(json.id_room);
        //             socket.Phong = json.id_room;
        //             io.sockets.in(json.id_room).emit("newuser", user);
        //             console.log(socket.Phong);
        //             console.log("Thanh Cong!");
        //         }
        //     }
        // });
    });
    //all user
    socket.on("alluser", function () {
        console.log("that all user");
        User.find(
            (err, doc) => {
                if (err) {
                    console.log("that bai");
                }
                else {
                    socket.emit("alluser", doc);
                    console.log("thanh cong");
                }
            }
        );
    });
    //user friend
    socket.on("alluserfriend", function () {
        console.log("that all user");
        UserFriends.find(
            (err, doc) => {
                if (err) {
                    console.log("that bai");

                }
                else {
                    socket.emit("alluserfriend", doc);
                    console.log("thanh cong");
                }
            }
        );
    });
    //create user friends
    socket.on("createUserFriend", function (data) {
        var ketqua = false;
        console.log("create friend");
        var json = JSON.parse(data);
        var userfriend = new UserFriends({
            friend_no: json.friend_no,
            userId1: json.userId1,
            userId2: json.userId2,
            users: json.users,
            regist_dt: json.regist_dt

        });
        userfriend.save((err) => {
            if (err) {

                console.log(" add user friends  fail");
            } else {
                UserFriends.findOne({ friend_no: json.friend_no }, function (err, data) {
                    if (err) {
                        console.log("fail ");
                        ketqua = false;
                    } else {
                        console.log("tc ");

                        callback(constants.success.msg_reg_success);
                        socket.UserFriends = "";
                        socket.UserFriends = data.friend_no;
                        ketqua = true;
                    }
                    socket.emit('ketquakb', { noidung: ketqua });
                });

            }
        })

    })
    //chat user
    socket.on("ChatUser", function (data) {
        io.sockets.in(socket.UserFriends).emit("ChatUser", data);
    });
    //user ready
    socket.on("ready", function (data) {
        io.sockets.in(socket.Phong).emit("ready", data);
        roomarr[socket.Phong].arrReady.push(data);
        if(roomarr[socket.Phong].arrReady.length==7){
            io.sockets.in(socket.Phong).emit("allready", data);
        }
    });
    //user exit
    socket.on("userexit", function (data) {
        Room.findOne({ _id: socket.Phong }, function (err, doc) {

            if (err) {
                console.log("That bai!");
            }
            else {
                doc.people = doc.users.length - 1;
                console.log(doc);
                doc.save((err) => {
                    if (err) {
                        console.log("That bai!");
                    }
                    else {
                        console.log("thanh cong!");
                    }
                });
                if (doc.users.length == 1) {
                    Room.deleteOne({ _id: socket.Phong }, function (err) {
                        if (err) {
                            console.log("That bai!");
                        }
                        else {
                            console.log("Thanh cong!");
                            socket.leave(socket.Phong);
                        }
                    });

                }
                else {
                    Room.update(
                        { _id: socket.Phong },
                        {
                            $pull: { users: { userId: data } }
                        },
                        { multi: true }, function (err) {
                            if (err) {
                                console.log("That Bai");
                            }
                            else {
                                console.log("Thanh cong !");
                                io.sockets.in(socket.Phong).emit("userexit", data);
                                socket.leave(socket.Phong);
                                socket.Phong="";
                                socket.host=0;
                            }
                        }
                    );
                }

            }
        });


    });

    //chat Server
    socket.on("ChatAll", function (data) {
        io.sockets.emit("ChatAll", data);
    });
    //kick user

    socket.on("kickuser", function (data) {
        io.sockets.in(socket.Phong).emit("leaveroom", data);
    });

    //Host
    socket.on("OK", function (data) {
        io.sockets.in(socket.Phong).emit("OK", data);
        roomarr[socket.Phong] = new RoomCache();
        //roomarr[socket.Phong].OK=data;
    });
    socket.on("ListNhanVat", function (data) {
        console.log(data);
        io.sockets.in(socket.Phong).emit("ListNhanVat", data);
        //roomarr[socket.Phong].arrNhanVat = data;
    });
    socket.on("Luot", function (data) {
        io.sockets.in(socket.Phong).emit("Luot", data);
        roomarr[socket.Phong].luot=data;
    });
    socket.on("IDBiBoPhieu", function (data) {
        io.sockets.in(socket.Phong).emit("IDBiBoPhieu", data);
        roomarr[socket.Phong].idBOPHIEU=data;
    });
    socket.on("UserBoPhieu", function (data) {
        io.sockets.in(socket.Phong).emit("UserBoPhieu", data);
    });
    socket.on("UserBoPhieuTat", function (data) {
        io.sockets.in(socket.Phong).emit("UserBoPhieuTat", data);
    });
    socket.on("UserDie", function (data) {
        io.sockets.in(socket.Phong).emit("UserDie", data);
        roomarr[socket.Phong].arrUserDie.push(data);
        roomarr[socket.Phong].idUserDie=data;
    });
    socket.on("NhanVatsang", function (data) {
        io.sockets.in(socket.Phong).emit("NhanVatsang", data);
    });
    socket.on("NhanVatTat", function (data) {
        io.sockets.in(socket.Phong).emit("NhanVatTat", data);
    });
    socket.on("AllChat", function (data) {
        io.sockets.in(socket.Phong).emit("AllChat", data);
    });
    socket.on("AllManHinhChon", function (data) {
        io.sockets.in(socket.Phong).emit("AllManHinhChon", data);
    });
    socket.on("BangIdChon", function (data) {
        var json = JSON.parse(data);
        io.sockets.in(socket.Phong).emit("BangIdChon", json.idchon);
        io.sockets.in(socket.Phong).emit("listallchon",json);
        roomarr[socket.Phong].arrAll.push(json.idchon)
    });
    socket.on("cuoingay",function(){
        socket.emit("cuoingay",roomarr[socket.Phong]);
    });
    socket.on("BangChonChucNang", function (data) {
        console.log(data);
        var json = JSON.parse(data);
        io.sockets.in(socket.Phong).emit(json.manv, json.idchon);
        if(json.manv=='1'){
            roomarr[socket.Phong].arrMaSoiChon.push(json.idchon);
        }else if(json.manv=='3'){
            roomarr[socket.Phong].idThoSanChon=json.idchon;
        }else if(json.manv=='4'){
            roomarr[socket.Phong].idBaoVeChon=json.idchon;
        }
    });
    socket.on("Chat", function (data) {
        io.sockets.in(socket.Phong).emit("Chat", data);
    });
    socket.on("BangBoPhieu", function (data) {
        var json = JSON.parse(data);
        console.log(data);
        io.sockets.in(socket.Phong).emit("BangBoPhieu", json.id);
        io.sockets.in(socket.Phong).emit("ListBangBoPhieu", json);
        roomarr[socket.Phong].arrKetQuaBoPhieu.push(json.id);
    });

    socket.on("resetngaymoi",function(){
        roomarr[socket.Phong] = new RoomCache();
    });
    
    
    socket.on("listdanlangchon",function(){
        socket.emit("listdanlangchon",roomarr[socket.Phong].arrAll);
    });

    socket.on("sync",function(data){
        io.sockets.in(socket.Phong).emit("sync", data);
    })

    socket.on("updatehost",function(){
        socket.host=1;
    });
    socket.on("time", function (data) {
        console.log(data);
        io.sockets.in(socket.Phong).emit("time", data);
    });
    socket.on("a", function (data) {
        console.log(data);
        //io.sockets.in(socket.Phong).emit("BangBoPhieu",data);
    });
    socket.on("userhostexit", function (data) {
        var id;
        Room.findOne({ _id: socket.Phong }, function (err, doc) {
            if (err) {
                console.log("That bai! 0");
            }
            else {
                doc.people = doc.users.length - 1;
                doc.host = 0;

                console.log(doc);
                doc.save((err) => {
                    if (err) {
                        console.log("That bai! 1");
                    }
                    else {
                        console.log("thanh cong!");
                        if (doc.users.length == 1) {
                            Room.deleteOne({ _id: socket.Phong }, function (err) {
                                if (err) {
                                    console.log("That bai! 2");
                                }
                                else {
                                    console.log("Thanh cong!");
                                    io.sockets.emit("DeleteRoom", socket.Phong);
                                    socket.leave(socket.Phong);


                                }
                            });


                        }
                        else {
                            id = doc.users[1].userId;
                            Room.update(
                                { _id: socket.Phong },
                                {
                                    $pull: { users: { userId: data } }
                                }, { multi: true }, function (err) {
                                    if (err) {
                                        console.log("That Bai 3");
                                    }
                                    else {
                                        console.log("Thanh cong !");
                                        io.sockets.in(socket.Phong).emit("userexit", data);
                                        io.sockets.in(socket.Phong).emit("useruphost", id);
                                        console.log(id);
                                        socket.leave(socket.Phong);
                                        socket.Phong="";
                                        socket.host=0;
                                    }
                                }
                            );
                        }
                    }
                });



            }
        });
    });

    socket.on("feedback", function (data) {
        var json = JSON.parse(data);
        var feedback = new FeedBack({
            email: json.email,
            message: json.message
        });
        feedback.save((err) => {
            if (err) {
                console.log("Failfull");
            }
            else {
                console.log("Success")
            }
        })
    });

    socket.on("updateuserinfo", function (data) {
        var json = JSON.parse(data);
        User.findOne({ userId: json.userId }, (err, doc) => {
            if (err) {
                console.log("Fail!");
            }
            else {
                doc.address = json.address;
                doc.birthday = json.birthday;
                doc.fullname = json.fullname;
                doc.email = json.email;
                doc.phone_number = json.phone_number;
                doc.save((err) => {
                    if (err) {
                        console.log("Fail");
                    } else {
                        console.log("Success");
                    }
                });
            }
        })
    });

    socket.on("finishgame", function (data) {
        var json = JSON.parse(data);
        var win = json.win;
        var gold = json.gold;
        var list = JSON.parse(json.list);
        console.log(data);
        if (win == '1') {
            list.forEach(element => {
                if (element.manv == '1') {
                    User.findOne({ userId: element.id }, (err, doc) => {
                        if (err) {
                            console.log("That Bai!");
                        }
                        else {
                            doc.win = doc.win + 1;
                            doc.money = doc.money+gold;
                            doc.save();
                        }

                    });
                }
                else {
                    User.findOne({ userId: element.id }, (err, doc) => {

                        if (err) {
                            console.log("That Bai!");
                        }
                        else {
                            doc.lose = doc.lose + 1;
                            doc.money = doc.money-gold;
                            doc.save();
                        }

                    });
                }
                console.log(element);
            });
        }
        else {
            list.forEach(element => {
                if (element.manv != '1') {
                    User.findOne({ userId: element.id }, (err, doc) => {
                        if (err) {
                            console.log("That Bai!");
                        }
                        else {
                            doc.win = doc.win + 1;
                            doc.money = doc.money+gold;
                            doc.save();
                        }

                    });
                }
                else {
                    User.findOne({ userId: element.id }, (err, doc) => {
                        if (err) {
                            console.log("That Bai!");
                        }
                        else {
                            doc.lose = doc.lose + 1;
                            doc.money = doc.money-gold;
                        doc.save();
                        }
                        
                    });
                }
            });
        
        }
        

    })

    socket.on("win", function (data) {
        io.sockets.in(socket.Phong).emit("win", data);
    });

    socket.on("ping", function (data) {
        console.log(data);
        var date = new Date();
        console.log(date);
        socket.emit("ping", date - Date.parse(data));
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

app.get("/", function (req, res) {
    res.render("index");
});
app.get("/image", function (req, res) {
    var imgPath = path.join(__dirname, 'image', 'aaa.jpg');
    var imgStream = fs.createReadStream(imgPath);
    res.writeHead(200, { "Content-Type": "image/jpeg" });
    imgStream.pipe(res);
});
app.get("/apk", function (req, res) {
    var imgPath = path.join(__dirname, 'public', 'app-debug.apk');
    var imgStream = fs.createReadStream(imgPath);
    res.writeHead(200, { "Content-Type": "application/vnd.android.package-archive" });
    imgStream.pipe(res);
});

let options = {
    db: { native_parser: true },
    server: { poolSize: 5 },
    user: 'admin',
    pass: 'admin'
};
mongoose.Promise = global.Promise;
mongoose.connect('mongodb://localhost:27017/MasoiDB', options).then(
    () => {
        console.log("connect Db Succes");
    },
    err => {
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

//start

