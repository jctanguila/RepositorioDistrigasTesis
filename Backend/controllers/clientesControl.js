const Cliente = require('../models/clienteModel');// Se importa el modelo Cliente

module.exports = {

     // Metodo para actualizar la ubicacion de un cliente
    actualizarUbicacion(req,res){
        const datos = req.body; // Obtiene los datos de ubicacion del cuerpo de la solicitud
        Cliente.actUbicacion(datos, (err, data) => { // Llama al metodo "actUbicacion" del modelo Cliente para actualizar la ubicacion del cliente
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

}