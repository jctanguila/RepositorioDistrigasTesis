const db = require('../config/config');

const Repartidor = {};

Repartidor.findByUsuario = (idUsuario, result) => {
    var sql = 'SELECT * FROM repartidores WHERE idUsuario=?'
    db.query(sql, [idUsuario], (err, res) => {
        if (err) {
            console.log(err)
            result(err, null)
        } else {            
            result(null, res[0])
        }
    })
}

Repartidor.findById = (id, result) => {
    var sql = 'SELECT * FROM repartidores WHERE id=?'
    db.query(sql, [id], (err, res) => {
        if (err) {
            console.log(err)
            result(err, null)
        } else {            
            result(null, res[0])
        }
    })
}

Repartidor.findAll = (datos, result) => {
    var sql = 'SELECT * FROM repartidores'
    db.query(sql, (err, res) => {
        if (err) {
            console.log(err)
            result(err, null)
        } else {            
            result(null, res)
        }
    })
}

Repartidor.actUbicacion = (datos, result) => {
    sql = 'UPDATE repartidores SET lat=?, lng=? WHERE idUsuario=?'
    db.query(sql, [ datos.lat, datos.lng, datos.idUsuario], (err, res) => {
        if (err) {
            result(err, null)
        } else {
            result(null, datos)
        }
    })
}

module.exports = Repartidor;