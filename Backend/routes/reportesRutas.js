const reporteControl = require('../controllers/reporteControl');

module.exports = (app) => {
   app.get('/api/reporte/reporteClientes', reporteControl.reporteClientes)
   app.get('/api/reporte/reporteVentas', reporteControl.reporteVentas)
   app.get('/api/reporte/reporteInventario', reporteControl.reporteInventario)
   
   app.post('/api/reporte/reporteVentasFecha', reporteControl.reporteVentasFecha) 
   app.post('/api/reporte/reporteClientesFecha', reporteControl.reporteClientesFecha) 
   app.post('/api/reporte/reporteInventarioFecha', reporteControl.reporteInventarioFecha) 
}