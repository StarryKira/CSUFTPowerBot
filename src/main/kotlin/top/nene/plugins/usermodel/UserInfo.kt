package top.nene.plugins.usermodel


class UserInfo(var wxOpenId:String,
               var DormitoryId:String,
               var elecMoney:Double,
               val email:String,
               var passwd:String,
               var coname:String) {
    init {
        wxOpenId = "0000000000000000000000000000"
        DormitoryId = "null"
        elecMoney = 0.00
    }
    //用户信息，邮箱，密码，当前电费，微信id



}