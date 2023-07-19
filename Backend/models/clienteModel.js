const db = require('../config/config');
const bcrypt = require('bcryptjs')

const Cliente = {};

Cliente.actUbicacion = (datos, result) => {
    var sql = 'UPDATE clientes SET lat=?, lng=? WHERE idUsuario=?'
    db.query(
        sql,
        [
            datos.lat,
            datos.lng,
            datos.idUsuario
        ], (err, res) => {
            if (err) {
                console.log(err)
                result(err, null)
            }
        })
    result(null, datos)
}

Cliente.findByUsuario = (idUsuario, result) => {
    var sql = 'SELECT * FROM clientes WHERE idUsuario=?'
    db.query(sql, [idUsuario], (err, res) => {
        if (err) {
            console.log(err)
            result(err, null)
        } else {            
            result(null, res[0])
        }
    })
}

module.exports = Cliente;