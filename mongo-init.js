db.createUser(
    {
        user  : "root",
        pwd   : "pass12345",
        roles : [
            {
                role : "readWrite",
                db   : "energy_drinks"
            }
        ]
    }
);