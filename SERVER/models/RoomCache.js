class RoomCache{
    constructor(){
        this.OK = false;
        this._id="";
        this.idBOPHIEU="";
        this.idUserDie="";
        this.idBaoVeChon="";
        this.idThoSanChon="";
        this.arrAll = new Array();
        this.arrMaSoiChon = new Array();
        this.arrBoPhieu =new Array();
        this.arrKetQuaBoPhieu = new Array();
        this.arrReady = new Array();
        this.arrUserDie = new Array();
        this.arrNhanVat="";
        this.luot = 1;
    }
}
module.exports = RoomCache;