const db = require('../config/config');

const Producto = {};

Producto.registro = async (producto, result) => {
    var sql = 'INSERT INTO productos(producto, descripcion, costo, pvp, imagen) VALUES(?,?,?,?,?)'
    db.query(
        sql,
        [
            producto.producto,
            producto.descripcion,
            producto.costo,
            producto.pvp,
            producto.foto
        ],
        (err, res) => {
            if (err) {
                console.log(err)
                result(err, null)
            } else {
                result(null, res.insertId)
            }
        }
    )
}

Producto.listaProductos = (req, result) => {
    const sql = `SELECT * FROM productos`
    db.query(sql, (err, res) => {
        if (err) {
            result(err, null)
        } else {
            result(null, res)
        }
    })
}

Producto.modificarProducto = (datos, result) => {
    sql = 'UPDATE productos SET producto=?, descripcion=?, costo=?, pvp=? WHERE id=?'
    db.query(
        sql,
        [
            datos.producto,
            datos.descripcion,
            datos.costo,
            datos.pvp,
            datos.id
        ], (err, res) => {
            if (err) {
                console.log(err)
                result(err, null)
            } else {
                result(null, datos)
            }
        })
}
Producto.eliProducto = (datos, result) => {
    sql = 'DELETE FROM productos WHERE id=?'
    console.log(datos.id)
    db.query(sql, [datos.id], (err, res) => {
        if (err) {
            result(err, null)
        } else {
            result(null, datos)
        }
    })
}

module.exports = Producto;