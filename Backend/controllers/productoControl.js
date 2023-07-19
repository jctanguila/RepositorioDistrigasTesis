const Producto = require('../models/productoModel');

module.exports = {
    registro(req, res) {
        const producto = req.body;
        console.log("en productosController:"+JSON.stringify(req.body))
        Producto.registro(producto, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "El Producto ha sido registrado exitosamente",
                data: data
            })
        })
    },
    listaProductos(req,res){
        Producto.listaProductos(req.body,(err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                data: data
            })
        })
    },
    modificarProducto(req,res){
        const producto = req.body;
        Producto.modificarProducto(producto, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Actualizacion realizada correctamente",
                data: data
            })
        })
    },
    eliProducto(req,res){
        const producto = req.body;
        console.log(producto.id)
        Producto.eliProducto(producto, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Eliminacion correcta",
                data: data
            })
        })
    }
}