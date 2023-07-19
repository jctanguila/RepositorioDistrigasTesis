const productoControl = require('../controllers/productoControl');
const upload = require('../libs/storage');

module.exports = (app) => {
    app.post('/api/productos/registro', upload.single('image'), productoControl.registro)
    app.post('/api/productos/modificarProducto', productoControl.modificarProducto)
    app.post('/api/productos/eliProducto', productoControl.eliProducto)
    
    app.get('/api/productos/listaProductos', productoControl.listaProductos)
}