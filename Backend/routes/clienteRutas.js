const clienteControl = require('../controllers/clientesControl')

module.exports = (app) => {
    app.post('/api/clientes/actualizarUbicacion', clienteControl.actualizarUbicacion)
}