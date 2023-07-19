const Reporte = require('../models/reporteModel');

module.exports = {
    reporteVentas(req,res){
        Reporte.reporteVentas(req.body,(err, data) => {
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
    reporteClientes(req,res){
        Reporte.reporteCliente(req.body,(err, data) => {
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
    reporteInventario(req,res){
        Reporte.reporteInventario(req.body,(err, data) => {
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
    reporteVentasFecha(req,res){
        const venta = req.body;
        Reporte.reporteVentasFecha(venta, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Lista de ventas",
                data: data
            })
        })
    },
    reporteInventarioFecha(req,res){
        const venta = req.body;
        Reporte.reporteInventarioFecha(venta, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Lista de ventas",
                data: data
            })
        })
    },
    reporteClientesFecha(req,res){
        const venta = req.body;
        Reporte.reporteClientesFecha(venta, (err, data) => {
            if (err) {
                return res.status(501).json({
                    success: false,
                    mensaje: "Hubo un error",
                    error: err
                })
            }
            return res.status(201).json({
                success: true,
                mensaje: "Lista de ventas",
                data: data
            })
        })
    },
}