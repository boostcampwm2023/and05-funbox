fetch('http://localhost:3000/users', {
    method : "POST",
    headers: {
        'Content-Type':'application/json'
    },
    body : JSON.stringify({locX:777.666666666666666,locY:666.6666666666666666})
}).then((res)=>{
    return res.json();
}).then((data)=>{
    console.log(data);
});

