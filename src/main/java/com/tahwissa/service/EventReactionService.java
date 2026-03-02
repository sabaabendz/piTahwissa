package com.tahwissa.service;

import com.tahwissa.dao.EventReactionDAO;
import com.tahwissa.entity.EventReaction;

import java.util.Optional;

public class EventReactionService {
    private final EventReactionDAO eventReactionDAO = new EventReactionDAO();

    public int getLikeCount(int idEvenement) {
        return eventReactionDAO.countByEventAndType(idEvenement, EventReaction.TYPE_LIKE);
    }

    public int getDislikeCount(int idEvenement) {
        return eventReactionDAO.countByEventAndType(idEvenement, EventReaction.TYPE_DISLIKE);
    }

    public Optional<String> getUserReactionType(int idEvenement, int idUser) {
        return eventReactionDAO.findTypeByEventAndUser(idEvenement, idUser);
    }

    /**
     * Toggle like: if user already liked, remove; otherwise set like (and clear dislike).
     */
    public void toggleLike(int idEvenement, int idUser) {
        Optional<String> current = eventReactionDAO.findTypeByEventAndUser(idEvenement, idUser);
        if (current.isPresent() && EventReaction.TYPE_LIKE.equals(current.get())) {
            eventReactionDAO.delete(idEvenement, idUser);
        } else {
            eventReactionDAO.upsert(new EventReaction(idEvenement, idUser, EventReaction.TYPE_LIKE));
        }
    }

    /**
     * Toggle dislike: if user already disliked, remove; otherwise set dislike (and clear like).
     */
    public void toggleDislike(int idEvenement, int idUser) {
        Optional<String> current = eventReactionDAO.findTypeByEventAndUser(idEvenement, idUser);
        if (current.isPresent() && EventReaction.TYPE_DISLIKE.equals(current.get())) {
            eventReactionDAO.delete(idEvenement, idUser);
        } else {
            eventReactionDAO.upsert(new EventReaction(idEvenement, idUser, EventReaction.TYPE_DISLIKE));
        }
    }
}
