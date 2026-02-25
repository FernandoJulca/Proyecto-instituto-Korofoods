export const enviroment = {
  production: false,
  apigateway: 'http://localhost:8098',
  apiUrls: {
    usuarios: 'http://localhost:8081',
    menu: 'http://localhost:8098',
    eventos: 'http://localhost:8098',
    resenas: 'http://localhost:8098', // usando el apigateway
    pedido: 'http://localhost:8086',
    pago:'http://localhost:8098',
    reserva: 'http://localhost:8098', // reserva y codigo de verificacion

    mesas: 'http://localhost:8098',
    usuarioSoap: 'http://localhost:8093'

  },
  githubClientId: 'Ov23liywDIDTbcViyzqf',
  githubRedirectUri: 'https://localhost:4200/auth/login',
};