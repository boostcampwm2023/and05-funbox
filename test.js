fetch('http://localhost:3000/users/location', {
    method : "POST",
    headers: {
        'Content-Type':'application/json'
    },
    body : JSON.stringify({locX:666.6666,locY:666.6666})
}).then((res)=>{
    return res.json();
}).then((data)=>{
    console.log(data);
});