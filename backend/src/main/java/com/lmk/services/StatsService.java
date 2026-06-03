/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.services;

import java.util.Map;

/**
 *
 * @author Acer
 */
public interface StatsService {
    Map<String, Object> getAdminStats();
    Map<String, Object> getInstructorStats(String username);
}
