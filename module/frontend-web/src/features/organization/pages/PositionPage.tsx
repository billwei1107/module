import { useEffect, useState } from 'react';
import { Box, Typography, List, ListItem, ListItemText } from '@mui/material';
import { organizationApi } from '../api/organizationApi';
import type { Position } from '../types';

const normalizeList = <T,>(payload: T[] | { data?: T[] }): T[] => {
    return Array.isArray(payload) ? payload : payload.data || [];
};

export const PositionPage = () => {
    const [positions, setPositions] = useState<Position[]>([]);

    useEffect(() => {
        organizationApi.getPositions().then(res => {
            setPositions(normalizeList<Position>(res));
        }).catch(console.error);
    }, []);

    return (
        <Box sx={{ p: 4 }}>
            <Typography variant="h5" sx={{ mb: 3, fontWeight: 'bold' }}>
                職位等級管理
            </Typography>
            <List>
                {positions.map(p => (
                    <ListItem key={p.id} sx={{ borderBottom: '1px solid #eee' }}>
                        <ListItemText
                            primary={`${p.name} (${p.code})`}
                            secondary={`層級：${p.level} | 敘述：${p.description || '無'}`}
                        />
                    </ListItem>
                ))}
            </List>
        </Box>
    );
};
