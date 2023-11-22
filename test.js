
fetch('http://localhost:3000/auth', {
    method : "POST",
    headers: {
        'Content-Type':'application/json'
    },
    body : JSON.stringify({accessToken:"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MzMsInVzZXJuYW1lIjpudWxsLCJpYXQiOjE3MDA2NzE2MjYsImV4cCI6MTcwMDkzMDgyNn0.qCpL-aelIhcPipjaol-MQ-L46yYjubyvV9xmq5fGAX4"})
}).then((res)=>{
    return res.json();
}).then((data)=>{
    console.log('------------------')
    console.log('1.1. 유효토큰 있음')
    console.log(data);
});

fetch('http://localhost:3000/auth', {
    method : "POST",
    headers: {
        'Content-Type':'application/json'
    },
    body : JSON.stringify({accessToken:"eJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MzMsInVzZXJuYW1lIjpudWxsLCJpYXQiOjE3MDA2NzE2MjYsImV4cCI6MTcwMDkzMDgyNn0.qCpL-aelIhcPipjaol-MQ-L46yYjubyvV9xmq5fGAX4"})
}).then((res)=>{
    return res.json();
}).then((data)=>{
    console.log('------------------')
    console.log('1.2. 유효토큰 없음')
    console.log(data);
});

fetch('http://localhost:3000/auth/notoken', {
    method : "POST",
    headers: {
        'Content-Type':'application/json'
    },
    body : JSON.stringify({naverAccessToken:"test3"})
}).then((res)=>{
    return res.json();
}).then((data)=>{
    console.log('------------------')
    console.log('2.1. 기사용자인 경우')
    console.log(data);
    console.log()
});

fetch('http://localhost:3000/auth/notoken', {
    method : "POST",
    headers: {
        'Content-Type':'application/json'
    },
    body : JSON.stringify({naverAccessToken:"test6"})
}).then((res)=>{
    return res.json();
}).then((data)=>{
    console.log('------------------')
    console.log('2.2. 신사용자인 경우')
    console.log(data);
    console.log()
});
