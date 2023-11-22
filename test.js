// fetch('http://localhost:3000/users', {
//     method : "POST",
//     headers: {
//         'Content-Type':'application/json'
//     },
//     body : JSON.stringify({locX:777.666666666666666,locY:666.6666666666666666})
// }).then((res)=>{
//     return res.json();
// }).then((data)=>{
//     console.log(data);
// });

fetch('http://localhost:3000/auth/signup', {
    method : "POST",
    headers: {
        'Content-Type':'application/json'
    },
    body : JSON.stringify({id_oauth:"test4"})
}).then((res)=>{
    return res.json();
}).then((data)=>{
    console.log(data);
});

fetch('http://localhost:3000/auth/login', {
    method : "POST",
    headers: {
        'Content-Type':'application/json'
    },
    body : JSON.stringify({id_oauth:"test3"})
}).then((res)=>{
    return res.json();
}).then((data)=>{
    console.log(data);
});

fetch('http://localhost:3000/auth', {
    method : "POST",
    headers: {
        'Content-Type':'application/json'
    },
    body : JSON.stringify({accessToken:"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MzMsInVzZXJuYW1lIjpudWxsLCJpYXQiOjE3MDA2NzE2MjYsImV4cCI6MTcwMDkzMDgyNn0.qCpL-aelIhcPipjaol-MQ-L46yYjubyvV9xmq5fGAX4"})
}).then((res)=>{
    return res.json();
}).then((data)=>{
    console.log(data);
});