const accessToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NzYsInVzZXJuYW1lIjpudWxsLCJpYXQiOjE3MDA3MjI3MTQsImV4cCI6MTcwMDk4MTkxNH0.NjbFZX6Nkt4UMO6LqO7uZpFbzX6VS5M7IUyQKGnxdGw'
const naverToken = "AAAAO64T70oTGtBGdxsTjyCHUqy6/8r5j8vwPnXmOSUj+icAzpR6CZX5e8rmOWVnzWcR4Z/YRH3L9bpKorRt/Y+xgko=";
                

fetch('http://localhost:3000/auth', {
    method : "POST",
    headers: {
        'Content-Type':'application/json'
    },
    body : JSON.stringify({accessToken: accessToken})
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
    body : JSON.stringify({accessToken: accessToken+"1"})
}).then((res)=>{
    return res.json();
}).then((data)=>{
    console.log('------------------')
    console.log('1.2. 유효토큰 없음')
    console.log(data);
});

fetch("http://localhost:3000/auth/notoken", {
        method : "POST",
        headers: {
           'Content-Type':'application/json'
        },
        body : JSON.stringify({naverAccessToken: naverToken})
    }).then((res)=>{
        return res.json();
    }).then((data)=>{
        console.log('------------------')
        console.log('2.1. 유효한 네이버 토큰')
        console.log(data);
});

fetch("http://localhost:3000/auth/notoken", {
        method : "POST",
        headers: {
           'Content-Type':'application/json'
        },
        body : JSON.stringify({naverAccessToken: naverToken+"1"})
    }).then((res)=>{
        return res.json();
    }).then((data)=>{
        console.log('------------------')
        console.log('2.2. 유효하지 않은 네이버 토큰')
        console.log(data);
});

// async function test(){
//     const naverToken2 = "AAAAO64T70oTGtBGdxsTjyCHUqy6/8r5j8vwPnXmOSUj+icAzpR6CZX5e8rmOWVnzWcR4Z/YRH3L9bpKorRt/Y+xgko="
//     const response = await fetch('https://openapi.naver.com/v1/nid/me', {
//         method : "GET",
//         headers : {
//             Authorization: 'Bearer '+ naverToken2
//         }
//     })
//     const userInfo = await response.json();
//     console.log(userInfo);
// }

// test();

//'https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=VMMWl9ZQ64Se75GQzlkd&redirect_uri=http://172.30.1.62:3000/&state=1'