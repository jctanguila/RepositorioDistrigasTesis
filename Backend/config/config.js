const mysql = require('mysql');

const db = mysql.createConnection({
    host:'localhost',
    user:'root',
    password:'',
    database:'distribuciongas'
});

db.connect(function(err){
    if(err) throw err;
    console.log("Conexion a la BD exitosa");
})

module.exports = db;