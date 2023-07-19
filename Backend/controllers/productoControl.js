const Producto = require('../models/productoModel');

module.exports = { // Metodo para registrar un nuevo producto
    registro(req, res) {
        const producto = req.body;
        console.log("en productosController:"+JSON.stringify(req.body))
        Producto.registro(producto, (err, data) => { // Llama al metodo "registro" del modelo Producto para registrar un nuevo producto
            if (err) {
                return res.status(501).json({ // Devuelve una respuesta de error con estado HTTP 501
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devuelve una respuesta exitosa con estado HTTP 201
                success: true,
                mensaje: "El Producto ha sido registrado exitosamente",
                data: data
            })
        })
    },
    listaProductos(req,res){ // Método para obtener la lista de productos
        Producto.listaProductos(req.body,(err, data) => { // Llama al metodo "listaProductos" del modelo Producto para obtener la lista de productos
            if (err) {
                return res.status(501).json({ // Devuelve una respuesta de error con estado HTTP 501
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devuelve una respuesta exitosa con estado HTTP 201
                success: true,
                data: data
            })
        })
    },
    modificarProducto(req,res){ // Metodo para modificar un producto existente
        const producto = req.body;
        Producto.modificarProducto(producto, (err, data) => { // Llama al metodo "modificarProducto" del modelo Producto para modificar un producto existente
            if (err) {
                return res.status(501).json({ // Devuelve una respuesta de error con estado HTTP 501
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devuelve una respuesta exitosa con estado HTTP 201
                success: true,
                mensaje: "Actualizacion realizada correctamente",
                data: data
            })
        })
    },
    eliProducto(req,res){ // Metodo para eliminar un producto
        const producto = req.body;
        console.log(producto.id)
        Producto.eliProducto(producto, (err, data) => { // Llama al metodo "eliProducto" del modelo Producto para eliminar un producto
            if (err) {
                return res.status(501).json({ // Devuelve una respuesta de error con estado HTTP 501
                    success: false,
                    mensaje: "hubo un error",
                    error: err
                })
            }
            return res.status(201).json({ // Devuelve una respuesta exitosa con estado HTTP 201
                success: true,
                mensaje: "Eliminacion correcta",
                data: data
            })
        })
    }
}