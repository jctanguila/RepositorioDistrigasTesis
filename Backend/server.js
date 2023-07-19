const express = require('express');
const app = express();
const HTTP = require('http');
const server = HTTP.createServer(app);
const logger = require('morgan');
const cors = require('cors');
const passport = require('passport');
const io = require('socket.io')(server);

/*
IMPORTAR SOCKETS
*/
const pedidosSocket = require('./sockets/pedidosSocket');

/*
IMPORTACION DE RUTAS
*/
const usuariosRoutes = require('./routes/usuarioRutas')
const productoRoutes = require('./routes/productoRutas')
const pedidoRoutes = require('./routes/pedidosRutas')
const clienteRoutes = require('./routes/clienteRutas')
const reportesRutas = require('./routes/reportesRutas')

const port = process.env.PORT || 3000;

app.set('port', port);
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({
    extended: true
}))
app.use(cors({ origin: '*' }))

app.use(passport.initialize())
app.use(passport.session())

pedidosSocket(io);

//Ruta para acceder a las imagenes
app.use(express.static('uploads'))

require('./config/passport')(passport)

app.disable('x-powered-by');

/*
LLAMADO DE RUTAS
*/
usuariosRoutes(app);
productoRoutes(app);
pedidoRoutes(app);
clienteRoutes(app);
reportesRutas(app);

server.listen(3000, '192.168.1.60' || 'localhost', function () {
    console.log("Servidor funcionando PID: " + process.pid + " en el puerto " + port)
});


//manejo de rutas de acceso

app.get('/', (req, res) => {
    res.send('ruta raiz del backend');
})


//manejo de errores

app.use((err, req, res, next) => {
    //200 - significa respuesta exitosa
    //400 - significa que la url no existe
    //500 - significa que ha existido un error interno en el servidor 
    console.log(err)
    res.status(err.status || 500).send(err.stack);
});