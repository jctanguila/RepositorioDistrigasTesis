const Cliente = require('../models/clienteModel');

module.exports = {

    actualizarUbicacion(req,res){
        const datos = req.body;
        Cliente.actUbicacion(datos, (err, data) => {
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

}