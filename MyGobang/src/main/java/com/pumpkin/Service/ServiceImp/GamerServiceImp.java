package com.pumpkin.Service.ServiceImp;

import com.pumpkin.Dao.DaoImp.GamerDaoImp;
import com.pumpkin.Service.GamerService;
import com.pumpkin.Service.UserService;
import com.pumpkin.entity.Gamer;

public class GamerServiceImp extends UserServiceImp implements GamerService {
    GamerDaoImp GamerDaoImp = new GamerDaoImp();
    @Override
    public Gamer getGamer(int id) {
        return GamerDaoImp.selectAllFromUser(id);
    }

    @Override
    public boolean updateGamerDan(Gamer gamer) {
        return GamerDaoImp.updateGamerDan(gamer);
    }

    @Override
    public boolean updateGamerMatchData(Gamer gamer) {
        return GamerDaoImp.updateGamerMatchData(gamer);
    }

    @Override
    public boolean clearSeasonMatchData(int id) {
        return GamerDaoImp.deleteGamerSeasonData(id);
    }

    @Override
    public boolean insertGamer(int id) {
        return GamerDaoImp.insertGamer(id);
    }

    @Override
    public Gamer selectGamerById(int id){
        return GamerDaoImp.selectAllFromUser(id);
    }
}
